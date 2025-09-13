def online_median(stream):
    import heapq

    min_heap = [] # Min-heap for the larger half
    max_heap = [] # Max-heap (inverted min-heap) for the smaller
    median = None
    medians = []
    for number in stream:
        if median is None:
            heapq.heappush(max_heap, -number)
            median = number
        elif number <= median:
            heapq.heappush(max_heap, -number)
        else:
            heapq.heappush(min_heap, number)

        # Balance the heaps
        if len(max_heap) > len(min_heap) + 1:
            heapq.heappush(min_heap, median)
            median = -heapq.heappop(max_heap)
        elif len(min_heap) > len(max_heap):
            heapq.heappush(max_heap, -median)
            median = heapq.heappop(min_heap)

        medians.append(median)
    return medians
if __name__ == "__main__":
    test_streams = [
        [5, 15, 1, 3],
        [2, 4, 6, 8, 10],
        [1, 2, 3, 4, 5, 6],
        [10, 20, 30]
    ]
    for stream in test_streams:
        print(f"Stream: {stream}, Medians: {online_median(stream)}")