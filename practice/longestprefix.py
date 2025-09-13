def longest_common_prefix(strs):
    if not strs:
        return ""
    # start with first string as the prefix
    prefix = strs[0]
    for s in strs[1:]:
        # reduce the prefix until it matches the start of s
        while not s.startswith(prefix):
            prefix = prefix[:-1]
            if not prefix:
                return ""
    return prefix
if __name__ == "__main__":
    test_cases = [
        ["flower", "flow", "flight"],
        ["dog", "racecar", "car"],
        ["interspecies", "interstellar", "interstate"],
    ]
    for strs in test_cases:
        print("Input:", strs)
        print("Longest Common Prefix:", longest_common_prefix(strs))