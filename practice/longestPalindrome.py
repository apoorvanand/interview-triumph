'''
Brute Force Approach to find the longest palindromic substring in a given string.
def longest_palindrome(s: str) -> str:
    if len(s) < 2:
        return s
    max_palindrome = ""
    for i in range(len(s)):
        for j in range(i+1, len(s)+1):
            substring = s[i:j]
            if substring == substring[::-1] and len(substring) > len(max_palindrome):
                max_palindrome = substring
    return max_palindrome

    - Expand Around Center Approach
    A more efficient method to find the longest palindromic substring is to expand around potential centers. A palindrome mirrors around its center, so we can check for palindromes by expanding outwards from each character (and between each pair of characters for even-length palindromes).
    - Iterate through each character in the string, treating each character (and the gap between characters) as a potential center of a palindrome.
    - For each center, expand outwards as long as the characters on both sides are equal.
    - Keep track of the longest palindrome found during these expansions.
def longest_palindrome_expand(s: str) -> str:
    if len(s) < 2:
        return s
    start, end = 0, 0
    for i in range(len(s)):
        len1 = expand_around_center(s, i, i)   # Odd length palindrome
        len2 = expand_around_center(s, i, i+1) # Even length palindrome
        max_len = max(len1, len2)
        if max_len > end - start:
            start = i - (max_len - 1) // 2
            end = i + max_len // 2
    return s[start:end+1]

'''