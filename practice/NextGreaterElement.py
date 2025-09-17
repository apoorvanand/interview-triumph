'''
Next Greater Element
Easy
Given an array of integers nums, find the next greater element for each element in nums. The next greater element of an element x is the first greater element to its right in the array.
'''
from typing import List
def nextGreaterElements(nums):
    n = len(nums)
    ans = [-1] * n
    stack = []
    for i, x in enumerate(nums):
        while stack and nums[stack[-1]] < x:
            idx = stack.pop()
            ans[idx] = x
        stack.append(i)
    return ans
'''
'''