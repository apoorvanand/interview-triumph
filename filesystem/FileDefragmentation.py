'''
Given a simulated disk with blocks and files fragmented across it, design an algorithm to defragment the disk. This means rearranging file blocks to be contigious, there by improving read/write performance. You will need to manage a list of free and used blocks
This problem is about space management and optimization algorithms 
'''
class Defragmenter:
    def __init__(self, disk_size: int, block_size:int):
        self.block_size = block_size
        self.total_blocks = disk_size//block_size
        self.disk = {}
        self.free_blocks = list(range(self.total_blocks))
    
    def allocate(self, file_id:str, num_blocks: int) -> list[int]:
        if len(self.free_blocks) < num_blocks:
            return []
        allocated_blocks = [self.free_blocks.pop(0)]