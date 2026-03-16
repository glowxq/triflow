#!/usr/bin/env python3
"""
Clean up exported SQL for open-source scaffold.
Merges data tables + structure-only tables into a clean init.sql.
"""
import re
import sys

DATA_FILE = "/tmp/triflow_data_tables.sql"
STRUCTURE_FILE = "/tmp/triflow_structure_tables.sql"
OUTPUT = "init.sql"

# bcrypt of 'root' - pre-generated
SAFE_PASSWORD = "$2a$10$ADwBAjEg4peTpiSdft9NxOwQr.jhOeADsAl6pnEtnao20xJ9qUmUW"


def strip_inserts(sql_content: str) -> str:
    """Remove all INSERT INTO statements, keep only DDL."""
    lines = sql_content.split("\n")
    result = []
    skip_insert = False

    for line in lines:
        # Start of an INSERT block
        if line.startswith("INSERT INTO "):
            skip_insert = True
            continue
        # Continuation of INSERT (multi-line values)
        if skip_insert:
            if line.startswith("(") or line.startswith(","):
                continue
            # End of INSERT block - line doesn't start with ( or ,
            skip_insert = False
        result.append(line)

    return "\n".join(result)


def strip_header(sql_content: str) -> str:
    """Remove the mysql-export tool header and DB creation commands."""
    lines = sql_content.split("\n")
    result = []
    skip = True

    for line in lines:
        # Skip until we see the first table structure comment
        if skip:
            if line.startswith("-- ------") or line.startswith("DROP TABLE"):
                skip = False
                result.append(line)
            continue
        result.append(line)

    return "\n".join(result)


def sanitize_data(sql_content: str) -> str:
    """Sanitize sensitive data in INSERT statements."""

    # 1. sys_user: remove the wx_ real user (id=10), keep root/admin/jack
    # Remove the real user line
    sql_content = re.sub(
        r",?\n\(10,\s*'wx_oZUGr7Vw_c2c7'.*?\);",
        ";",
        sql_content,
        flags=re.DOTALL
    )

    # 2. sys_user: replace passwords with standard bcrypt of 'root'
    # Match password field in sys_user INSERT
    sql_content = re.sub(
        r"('\$2a\$10\$[A-Za-z0-9./]{53}')",
        f"'{SAFE_PASSWORD}'",
        sql_content
    )

    # 3. file_config: replace aliyun OSS with MinIO config
    # Remove old aliyun config line, keep only local (id=1) and add MinIO
    sql_content = re.sub(
        r",?\n\(2,\s*'配置test',\s*'aliyun'.*?\);",
        ",\n(2, 'MinIO存储', 'minio', 'minio', 'http://minio:9000', 'minioadmin', 'minioadmin', 'triflow', NULL, NULL, '/upload', 0, 1, 1, 'MinIO对象存储，适用于Docker部署环境', NULL, NULL, NULL, NULL, NULL, NULL, 0);",
        sql_content,
        flags=re.DOTALL
    )

    # 4. sys_user_role: remove the mapping for user id=10
    sql_content = re.sub(
        r",?\n\(\d+,\s*10,\s*\d+,\s*NULL\)",
        "",
        sql_content
    )

    # 5. Remove real phone numbers (replace with safe ones)
    # Already using safe phone numbers for root/admin/jack (138xxxx)

    # 6. Remove real OSS URLs from avatars
    sql_content = sql_content.replace(
        "https://triflow-base.oss-cn-shenzhen.aliyuncs.com/avatar/jpg/2026/01/28/wx-avatar-1769609737942.jpg",
        "https://api.dicebear.com/9.x/bottts-neutral/svg?seed=demo"
    )

    return sql_content


def clean_auto_increment(sql_content: str) -> str:
    """Reset AUTO_INCREMENT values in CREATE TABLE statements."""
    return re.sub(
        r"AUTO_INCREMENT=\d+\s+",
        "AUTO_INCREMENT=1 ",
        sql_content
    )


def remove_empty_record_sections(sql_content: str) -> str:
    """Remove empty 'Records of' comment sections (no INSERT follows)."""
    # Remove consecutive empty lines (more than 2)
    sql_content = re.sub(r"\n{4,}", "\n\n\n", sql_content)
    return sql_content


def main():
    # Read both files
    with open(DATA_FILE, "r", encoding="utf-8") as f:
        data_sql = f.read()

    with open(STRUCTURE_FILE, "r", encoding="utf-8") as f:
        structure_sql = f.read()

    # Process structure tables: strip all INSERT data, keep only DDL
    structure_clean = strip_inserts(structure_sql)
    structure_clean = strip_header(structure_clean)
    structure_clean = clean_auto_increment(structure_clean)

    # Process data tables: sanitize sensitive info
    data_clean = strip_header(data_sql)
    data_clean = sanitize_data(data_clean)
    data_clean = clean_auto_increment(data_clean)

    # Build final output
    header = """-- =====================================================
-- Triflow 数据库初始化脚本
-- 用途: Docker 首次启动时自动初始化数据库
-- 说明: 包含所有表结构和基础数据 (菜单/角色/用户等)
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

"""

    footer = """
SET FOREIGN_KEY_CHECKS = 1;
"""

    # Merge: structure tables first, then data tables
    content = structure_clean + "\n\n" + data_clean
    content = remove_empty_record_sections(content)

    with open(OUTPUT, "w", encoding="utf-8") as f:
        f.write(header + content + footer)

    # Stats
    table_count = content.count("DROP TABLE IF EXISTS")
    insert_count = content.count("INSERT INTO")
    line_count = content.count("\n")
    print(f"✅ Clean SQL written to {OUTPUT}")
    print(f"   Tables: {table_count}")
    print(f"   INSERT statements: {insert_count}")
    print(f"   Lines: {line_count}")


if __name__ == "__main__":
    main()
