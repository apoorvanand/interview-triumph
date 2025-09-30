'''
given an unsorted array arr of n positive and negative numbers. your task is
to create an
array of alternate positive and negative numbers without changing the relative order of
positive and negative numbers.
note: array should start with positive number.
arr[] {9, 4, -2, -1, 5, 0, -5, -3, 2}
output: {9, -2, 4, -1, 5, -5, 0, -3, 2}
without using extra space. 
'''
def alternate(arr, n):
    pos = 1
    neg = 0
    while True:
        while pos < n and arr[pos] >= 0:
            pos += 2
        while neg < n and arr[neg] <= 0:
            neg += 2
        if pos < n and neg < n:
            arr[pos], arr[neg] = arr[neg], arr[pos]
        else:
            break
    return arr 
arr = [9, 4, -2, -1, 5, 0, -5, -3, 2]
n = len(arr)
print(alternate(arr, n))
#time complexity: O(n^2)
#space complexity: O(1)
#efficient approach
'''
1. count positive and negative numbers
2. rearrange the array in-place
'''
def alternate_efficient(arr, n):
    pos_count = 0
    neg_count = 0
    for i in range(n):
        if arr[i] >= 0:
            pos_count += 1
        else:
            neg_count += 1
    pos_index = 0
    neg_index = 1
    while pos_count > 0 and neg_count > 0:
        while pos_index < n and arr[pos_index] >= 0:
            pos_index += 2
        while neg_index < n and arr[neg_index] <= 0:
            neg_index += 2
        if pos_index < n and neg_index < n:
            arr[pos_index], arr[neg_index] = arr[neg_index], arr[pos_index]
            pos_count -= 1
            neg_count -= 1
        else:
            break
    return arr
arr = [9, 4, -2, -1, 5, 0, -5, -3, 2]
n = len(arr)
print(alternate_efficient(arr, n))
#time complexity: O(n)
#space complexity: O(1)