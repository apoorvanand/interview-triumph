'''
Group Anagrams
Given an array of strings, group anagrams together.
'''
from collections import defaultdict
from typing import List
def group_anagrams(strs: List[str]) -> List[List[str]]:
    """
    Groups anagrams from the input list of strings.
    Time Complexity: O(n * k log k) where n is the number of strings and k is the maximum length of a string.
    Space Complexity: O(n) for storing the grouped anagrams.
    """
    anagrams = defaultdict(list)
    for s in strs:
        sorted_str = ''.join(sorted(s)) # Sort the string to find its anagram key
        anagrams[sorted_str].append(s)
    return list(anagrams.values())
if __name__ == "__main__":
    test_cases = [
        ["eat", "tea", "tan", "ate", "nat", "bat"],
        [""],
        ["a"],
        ["abc", "bca", "cab", "xyz", "zyx"]
    ]
    for strs in test_cases:
        print(f"Input: {strs}, Grouped Anagrams: {group_anagrams(strs)}")