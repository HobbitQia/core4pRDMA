
def read_data_from_file(filename):
    with open(filename, 'r') as file:
        lines = file.readlines()

    data = []

    for line in lines:
        if '|' in line:
            line = line.split('|')[0].strip()  # 忽略 | 后面的字符
            line_data = line.split()[:]  # 忽略开头的八位地址
            data.extend(line_data)

    return data


def format_hex_string(hex_string):
    hex_string = hex_string.zfill(8)  # 前导零补齐为八位
    return hex_string


def process_data(data):
    processed_data = []

    for i in range(0, len(data), 17):
        for k in range(4):
            x = int(data[i], 16) // 4 + k
            # 地址不要有前面的 x
            address = format_hex_string(hex(x)[2:])
            hex_string = ''.join(data[i+k*4+1 : i+k*4+5])
            hex_pairs = [hex_string[j:j+2] for j in range(0, len(hex_string), 2)]
            reversed_hex_string = ''.join(hex_pairs[::-1])
            processed_data.append('@' + address + ' ' + reversed_hex_string)
            

    return processed_data


def write_data_to_file(filename, processed_data):
    with open(filename, 'w') as file:
        for item in processed_data:
            file.write(item + '\n')





data = read_data_from_file("inst.txt")
processed_data = process_data(data)
write_data_to_file("inst.mem", processed_data)

# 指定输入文件路径
# file_path = "inst.txt"
# read_data_from_file(file_path, "inst.mem")
# file_path = "data.txt"
# process_hex_file(file_path, "data.mem")
