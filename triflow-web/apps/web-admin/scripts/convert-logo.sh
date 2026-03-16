#!/bin/bash

# =====================================================
# Triflow Logo 转换脚本
# 将 SVG 转换为 PNG 和 ICO 格式
# =====================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PUBLIC_DIR="$SCRIPT_DIR/../public"

echo "🎨 开始转换 Triflow Logo..."

# 检查 rsvg-convert 是否可用
if ! command -v rsvg-convert &> /dev/null; then
    echo "❌ 错误: 需要安装 librsvg"
    echo "   macOS: brew install librsvg"
    echo "   Ubuntu: apt-get install librsvg2-bin"
    exit 1
fi

# 转换 SVG 到多种尺寸的 PNG
echo "📐 生成不同尺寸的 PNG..."

# 生成 favicon 尺寸 (16, 32, 48, 64, 128, 256, 512)
for size in 16 32 48 64 128 256 512; do
    rsvg-convert -w $size -h $size "$PUBLIC_DIR/logo.svg" -o "$PUBLIC_DIR/logo-${size}.png"
    echo "   ✓ logo-${size}.png"
done

# 复制一个标准尺寸作为 logo.png
cp "$PUBLIC_DIR/logo-256.png" "$PUBLIC_DIR/logo.png"
echo "   ✓ logo.png (256x256)"

# 生成 ICO 文件
echo "🔄 生成 ICO 文件..."

# 检查 Python 和 Pillow 是否可用
if python3 -c "from PIL import Image" 2>/dev/null; then
    python3 << 'EOF'
from PIL import Image
import os

public_dir = os.environ.get('PUBLIC_DIR', './public')
if not os.path.isabs(public_dir):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    public_dir = os.path.join(script_dir, '..', 'public')

# 读取不同尺寸的 PNG
sizes = [16, 32, 48, 64]
images = []

for size in sizes:
    png_path = f"{public_dir}/logo-{size}.png"
    if os.path.exists(png_path):
        img = Image.open(png_path)
        images.append(img)

if images:
    # 保存为 ICO
    ico_path = f"{public_dir}/favicon.ico"
    images[0].save(ico_path, format='ICO', sizes=[(img.width, img.height) for img in images])
    print(f"   ✓ favicon.ico (多尺寸: {', '.join(str(s) for s in sizes)})")
else:
    print("   ⚠ 未找到 PNG 文件，无法生成 ICO")

EOF
else
    echo "   ⚠ 需要 Pillow 库来生成 ICO"
    echo "   安装: pip3 install Pillow"
    echo "   跳过 ICO 生成..."
fi

# 清理临时文件（保留常用尺寸）
echo "🧹 清理临时文件..."
for size in 48 64 128; do
    rm -f "$PUBLIC_DIR/logo-${size}.png"
done

echo ""
echo "✅ Logo 转换完成！"
echo "   生成的文件:"
echo "   - public/logo.svg (源文件)"
echo "   - public/logo-dark.svg (暗色主题)"
echo "   - public/logo.png (256x256)"
echo "   - public/logo-16.png (16x16)"
echo "   - public/logo-32.png (32x32)"
echo "   - public/logo-256.png (256x256)"
echo "   - public/logo-512.png (512x512)"
echo "   - public/favicon.ico (多尺寸)"
