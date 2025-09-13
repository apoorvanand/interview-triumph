def sort_k_increasing_decreasing_array(arr):
    import heapq

    if not arr:
        return []
    
    result = []
    min_heap = []
    n = len(arr)
    i = 0
    is_increasing = True
    while i < n:
        start = i
        while i + 1 < n and ((arr[i] <= arr[i + 1]) if is_increasing else (arr[i] >= arr[i + 1])):
            i += 1
        end = i + 1
        subarray = arr[start:end]
        if not is_increasing:
            subarray.reverse()
        for num in subarray:
            heapq.heappush(min_heap, num)
        while min_heap:
            result.append(heapq.heappop(min_heap))
        is_increasing = not is_increasing
        i += 1
    return result
if __name__ == "__main__":
    test_arrays = [
        [1, 3, 5, 4, 2, 6, 8, 7],
        [10, 20, 15, 25, 30, 28, 35],
        [5, 10, 15],
        [30, 20, 10],
        []
    ]
    for arr in test_arrays:
        print(f"Original: {arr}, Sorted: {sort_k_increasing_decreasing_array(arr)}")
        