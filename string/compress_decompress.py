'''
File Compression and Decompression
Implement a simple compression algorithm, like Run-Length Encoding (RLE), or a more advanced one, like a simplified Huffman coding, to compress a file. Then, write the corresponding decompression function.

This tests your knowledge of data compression algorithms and your ability to manage bit-level operations and file streams.


'''
def compress_rle(data:str) -> str:
    if not data:
        return ""
    compressed = []
    count = 1
    for i in range(1, len(data)):
        if data[i] == data[i-1]:
            count += 1
        else:
            compressed.append(str(count) + data[i-1])
            count = 1
    compressed.append(str(count) + data[-1])
    return ''.join(compressed)

def decompress_rle(data:str) -> str:
    if not data:
        return ""
    decompressed =[]
    i = 0
    while i < len(data):
        count_str = ""
        while i < len(data) and data[i].isdigit():
            count_str += data[i]
            i += 1
        count = int(count_str)
        char = data[i]
        decompressed.append(char * count)
        i += 1
    return "".join(decompressed)

