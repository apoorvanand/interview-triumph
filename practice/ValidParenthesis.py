'''
Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.

'''
class Solution:
    def isValid(self,s:str) -> bool:
        stack = []
        map = {')':'(', '}':'{', ']':'['}
        for char in s:
            if char in map:
                top_element= stack.pop() if stack else '#'
                if map[char] != top_element:
                    return False
            else:
                stack.append(char)
        return not stack
# Example usage
if __name__ == "__main__":
    sol = Solution()
    test_cases = ["()", "()[]{}", "(]", "([)]", "{[]}", "", "((()))", "({[()]})", "((())", "())"]
    for s in test_cases:
        print(f"isValid('{s}') = {sol.isValid(s)}")
        