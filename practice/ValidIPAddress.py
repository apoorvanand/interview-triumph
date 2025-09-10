'''
Valid IP Addresses Problem
The "Valid IP Addresses" problem is a classic string manipulation and backtracking problem. Given a string containing only digits, the goal is to find all possible valid IP addresses that can be formed by inserting dots into the string. A valid IP address has four parts, each ranging from 0 to 255.

Understanding the Rules
An IP address (specifically, IPv4) is a string of four numbers separated by dots, like 192.168.1.1. Each number must satisfy the following conditions:

Number of parts: There must be exactly four parts (or octets).

Valid range: Each part must be a number between 0 and 255, inclusive.

No leading zeros: A part cannot have leading zeros unless the number itself is 0. For example, 01 is invalid, but 0 is valid.

Algorithmic Approach: Backtracking
The most common and effective way to solve this problem is using a backtracking or recursive approach. The core idea is to recursively build the IP address part by part.

Here's the step-by-step logic:

Base Case: The recursion stops when we have used up all characters of the input string and have successfully formed exactly four parts of the IP address. If both conditions are met, we have found a valid IP address.

Recursive Step: At each step of the recursion, we try to form the next part of the IP address. From the current position in the input string, we can take a substring of length 1, 2, or 3 to form the next part.

Validation: Before making the recursive call, we must validate the chosen substring to ensure it forms a valid number for an IP part:

The number must not be empty.

It must be in the range [0, 255].

If the length of the substring is greater than 1, it cannot start with a 0.

Pruning: We can prune the search space to avoid unnecessary computations. For example, if we have already added three parts to our IP address and there are more than three characters left in the input string, it's impossible to form the fourth valid part, so we can stop that branch of the recursion.
'''
def restoreIpAddresses(s: str) -> list[str]:
    result = []
    
    def backtrack(index, current_path):
        # Base case: we have formed 4 parts
        if len(current_path) == 4:
            # Check if all characters have been used
            if index == len(s):
                result.append(".".join(current_path))
            return

        # Recursive step: try adding a new part
        for i in range(1, 4):
            # Pruning: check if substring length is valid and within bounds
            if index + i > len(s):
                break
            
            segment = s[index:index + i]

            # Validate the segment
            if is_valid(segment):
                current_path.append(segment)
                backtrack(index + i, current_path)
                current_path.pop() # Backtrack

    def is_valid(segment):
        # Check for leading zeros and range
        if len(segment) > 1 and segment.startswith('0'):
            return False
        
        return 0 <= int(segment) <= 255

    backtrack(0, [])
    return result