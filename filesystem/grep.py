import os
import re
def grep(pattern: str, path: str, recursive: bool = False, case_insensitive: bool = False):
    regex = re.compile(pattern, re.IGNORECASE if case_insensitive else 0)

    if os.path.isfile(path):
        _search_file(path, regex)
    elif os.path.isdir(path):
        for dirpath, _, filenames in os.walk(path):
            for filename in filenames:
                file_path = os.path.join(dirpath, filename)
                _search_file(file_path, regex)
            if not recursive:
                break
    else:
        print(f"Error: {path} not found.")
def _search_file(file_path, regex):
    try:
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            for line_num, line in enumerate(f,1):
                if regex.search(line):
                    print(f"{file_path}:{line_num}: {line.strip()}")
    except IOError as e:
        print(f"Error reading file {file_path}: {e}") 