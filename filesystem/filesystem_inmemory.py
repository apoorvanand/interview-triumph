'''
### 1. **Implement a File System in Memory**
You're asked to build a simple in-memory file system. This involves designing data structures for files (storing content and metadata) and directories (storing references to files and other directories). The core operations to implement are:
* `create(path, content)`: Creates a new file at a given path.
* `read(path)`: Reads the content of a file.
* `write(path, content)`: Writes to an existing file.
* `mkdir(path)`: Creates a new directory.
* `ls(path)`: Lists the contents of a directory.

This problem tests your understanding of **tree data structures** and path traversal. 
'''
class InMemoryFileMemorySystem:
    def __init__(self):
        self.root = {"type": "directory", "children": {}}

    def __get__parent_and_name(self, path:str):
        """ Helper to get a node's parent and its name"""
        parts = path.strip('/').split('/')
        if not parts or parts == ['']:
            return self.root, "" # Root directory case
        parent_node = self.root
        for part in parts[:-1]:
            if part not in parent_node['children'] or parent_node['children'] [part]['type'] !='directory':
                return None, None
            parent_node = parent_node['children'][part]
        return parent_node, parts[-1]
    def __get_node(self, path: str):
        """Finds a node at the given path."""
        parent, name = self.__get__parent_and_name(path)
        if not parent or name not in parent['children']:
            return None
        return parent['children'][name]
    def create(self, path:str, content: str):
        parent, name = self.__get__parent_and_name(path)
        if not parent or name in parent['children']:
            return False
        parent['children'][name] = {"type":"file", "content": content}
        return True
    def read(self, path:str):
        node = self.__get_node(path)
        if node and node['type'] == 'file':
            return node['content']
        return None
    def write(self, path:str, content:str):
        node = self.__get_node(path)
        if node and node['type'] == 'file':
            node['content'] = content
            return True
        return False

    def mkdir(self, path:str):
        parent, name = self.__get__parent_and_name(path)
        if not parent or name in parent['children']:
            return False
        parent['children'][name] = {"type": "directory", "children": {}}
        return True
    def ls(self, path:str):
        node = self.__get_node(path)
        if node and node['type'] == 'directory':
            return sorted(list(node['children'].keys()))
        return []
class FileSystemTests:
    def __init__(self, filesystem):
        self.fs = filesystem
    
    def _assert_equal(self, actual, expected, message=""):
        assert actual == expected, f"Test failed: Expected {expected}, got {actual} {message}"
        print(f"Test passed: {message}")
    
if __name__ =="main":
    # Assuming Optimised In Memory System 
    fs = InMemoryFileMemorySystem()
    tester = FileSystemTests(fs)
    
