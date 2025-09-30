'''


Design Filesystem 

Design a simple in-memory file filesystem that supports basic file operations such as creating files and directories, deleting files
and directories, listing directory contents, and navigating through directories

Assumptions -
1. In memory 
2. Basic operations
3. 
'''
class Filesystem:
    def __init__(self):
        self.root = {}

    def ls(self, path: str) -> list:
        components = path.strip().split('/')
        current_dir = self.root
        for component in components[1:]:
            if component == '':
                continue
            if component not in current_dir:
                raise FileNotFoundError(f"Path '{path}' does not exist")
            current_dir = current_dir[component]
        if isinstance(current_dir, dict):
            return list(current_dir.keys())
        else:
            return [components[-1]]

    def mkdir(self, path: str) -> None:
        components = path.strip().split('/')
        current_dir = self.root
        for component in components[1:]:
            if component == '':
                continue
            if component not in current_dir:
                current_dir[component] = {}
            current_dir = current_dir[component]

    def addContentToFile(self, filePath: str, content: str) -> None:
        components = filePath.strip().split('/')
        filename = components[-1]
        current_dir = self.root
        for component in components[:-1]:
            if component == '':
                continue
            if component not in current_dir:
                raise FileNotFoundError(f"Path '{filePath}' does not exist")
            current_dir = current_dir[component]
        if filename not in current_dir:
            current_dir[filename] = content
        else:
            current_dir[filename] += content

    def readContentFromFile(self, filePath: str) -> str:
        components = filePath.strip().split('/')
        filename = components[-1]
        current_dir = self.root
        for component in components[:-1]:
            if component == '':
                continue
            if component not in current_dir:
                raise FileNotFoundError(f"Path '{filePath}' does not exist")
            current_dir = current_dir[component]
        return current_dir.get(filename, '')

    def rm(self, path: str) -> None:
        components = path.strip().split('/')
        filename_or_dir = components[-1]
        current_dir = self.root
        for component in components[:-1]:
            if component == '':
                continue
            if component not in current_dir:
                raise FileNotFoundError(f"Path '{path}' does not exist")
            current_dir = current_dir[component]
        if filename_or_dir in current_dir:
            del current_dir[filename_or_dir]
        else:
            raise FileNotFoundError(f"Path '{path}' does not exist")

# Example usage
fs = Filesystem()
fs.mkdir('/newdir')
fs.addContentToFile('/newdir/newfile.txt', 'Hello, World!')
print(fs.ls('/'))  # Output: ['newdir']
print(fs.readContentFromFile('/newdir/newfile.txt'))  # Output: 'Hello, World!'
fs.rm('/newdir')
        