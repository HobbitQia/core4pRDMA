
def read_data_from_file(filename):
    with open(filename, 'r') as file:
        lines = file.readlines()

    data = []

    for line in lines:
        
        line_data = line.split()[:]  # 忽略开头的八位地址
        data.extend(line_data)

    return data


def format_hex_string(hex_string):
    hex_string = hex_string.zfill(8)  # 前导零补齐为八位
    return hex_string


def process_data(data):
    processed_data = []
    address = -1
    for i in range(0, len(data)):
        # 地址不要有前面的 x
        address = address + 1
        hex_string = ''.join(data[i])
        # hex_pairs = [hex_string[j:j+2] for j in range(0, len(hex_string), 2)]
        # reversed_hex_string = ''.join(hex_pairs[::-1])
        processed_data.append('@' + str(hex(address))[2:] + ' ' + format_hex_string(hex_string[2:]))
            

    return processed_data


def write_data_to_file(filename, processed_data):
    with open(filename, 'w') as file:
        for item in processed_data:
            file.write(item + '\n')





data = read_data_from_file("inst.txt")
processed_data = process_data(data)
write_data_to_file("inst.mem", processed_data)
