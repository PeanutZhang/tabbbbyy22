#!/bin/bash
input_folder=$1  # 输入文件夹参数
output_folder=$2  # 输出文件夹参数
# 检查输入参数是否为空
if [ -z "$input_folder" ] || [ -z "$output_folder" ]; then
  echo "请提供输入文件夹和输出文件夹参数"
  exit 1
fi
# 检查输入文件夹是否存在
if [ ! -d "$input_folder" ]; then
  echo "输入文件夹不存在"
  exit 1
fi
# 如果输出文件夹不存在，则创建输出文件夹
if [ ! -d "$output_folder" ]; then
  mkdir -p "$output_folder"
fi
# 遍历输入文件夹下的以 .dex 结尾的文件，并转换为 .jar
for file in "$input_folder"/*.dex; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    echo "--b> $filename"
    filename="${filename%.*}"
    echo "--a> $filename"
    jar_file="$output_folder/$filename.jar"
    echo "jar_file_path-> $jar_file"
    d2j-dex2jar.sh -o "$jar_file" "$file"
    if [ $? == 0 ];then
     echo "已转换文件: $file -> $jar_file"
     rm "$file"
    else
      echo "转换失败---"
    fi
  fi
done