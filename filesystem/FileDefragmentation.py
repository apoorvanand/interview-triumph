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
        allocated_blocks = [self.free_blocks.pop(0) for _ in range(num_blocks)]
        for block_id in allocated_blocks:
            self.disk[block_id] = file_id
        return allocated_blocks
    def defragment(self):
        new_disk = {}
        used_blocks = sorted(self.disk.keys())
        # Shift all used blocks to the begining of the disk
        new_free_count = self.total_blocks
        for i, block_id in enumerate(used_blocks):
            new_disk[i]= self.disk[block_id]
            new_free_count -= 1
        self.disk = new_disk
        self.free_blocks = list(range(self.total_blocks - new_free_count - self.total_blocks))
        print("Disk defragmented. Free blocks are now contiguous")
        