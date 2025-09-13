def sort_k_increasing_decreasing_array(arr):
    import heapq

    if not arr:
        return []
    
    result = []
    min_heap = []
    n = len(arr)
    i = 0
    is_