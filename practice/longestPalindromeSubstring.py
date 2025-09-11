'''
Longest Palindromic Substring
Medium
Given a string s, return the longest palindromic substring in s.

'''
def longest_palindrome(s: str) -> str:
    """
    Brute Force Approach to find the longest palindromic substring.
    Time Complexity: O(n^3) because of the nested loops and substring slicing/reversal.
    Space Complexity: O(1) besides the storage for the result.
    """
    if len(s) < 2:
        return s
    max_palindrome = ""
    n = len(s)
    for i in range(n):
        for j in range(i +1, n +1):
            substring = s[i:j]
            if substring == substring[::-1]: # Check if the substring is a palindrome
                if len(substring) > len(max_palindrome):
                    max_palindrome = substring
    return max_palindrome

def longest_palindrome_expand(s: str) -> str:
    """
    Expand Around Center Approach.
    A more efficient method to find the longest palindromic substring.
    A palindrome mirrors around its center, so we can check for palindromes by
    expanding outwards from each character (and between each pair of characters
    for even-length palindromes).
    Time Complexity: O(n^2)
    Space Complexity: O(1)
    """
    if not s or len(s) < 2:
        return s
    start, end = 0, 0
    for i in range(len(s)):
        len1 = expand_around_center(s, i, i)   # Odd length palindrome (e.g., "aba")
        len2 = expand_around_center(s, i, i+1) # Even length palindrome (e.g., "abba")
        max_len = max(len1, len2)
        if max_len > end - start:
            start = i - (max_len - 1) // 2
            end = i + max_len // 2
    return s[start:end+1]

def expand_around_center(s: str, left: int, right: int) -> int:
    """
    Helper function to expand around the center and return the length of the palindrome.
    """
    while left >= 0 and right < len(s) and s[left] == s[right]:
        left -= 1
        right += 1
    return right - left - 1

if __name__ == "__main__":
    test_cases = ["babad", "cbbd", "a", "ac", "racecar"]
    for s in test_cases:
        print(f"String: '{s}', Longest Palindrome: '{longest_palindrome_expand(s)}'")