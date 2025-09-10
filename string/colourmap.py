def dutch_flag_sort(nums):
    color_map = {'red':0,'white':1, 'black':2}
    low, mid, high = 0, 0, len(nums) -1
    while mid <= high:
        color = nums[mid]
        if color_map[color] == 0: # red
            nums[low], nums[mid] = nums[mid], nums[low]
            low += 1
            mid += 1
        elif color_map[color] == 1: # white
            mid += 1
        else: # black
            nums[mid], nums[high] = nums[high], nums[mid]
            high -= 1
    return nums
if __name__ == "__main__":
    arr = ['white', 'black', 'red', 'white', 'red', 'black', 'red']
    print(dutch_flag_sort(arr))  # Output should be ['red', 'red', 'red', 'white', 'white', 'black', 'black']'''

        