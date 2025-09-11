'''
Group Anagrams
Given an array of strings, group anagrams together.
'''
package practice

import (
	"sort"
	"strings"
)

func groupAnagrams(strs []string) [][]string {
	anagramsMap := make(map[string][]string)
	for _, str:= range strs{
		sortedStr := strings.Split(str, "")
		sort.Strings(sortedStr)
		key := strings.Join(sortedStr, "")
		anagramsMap[key] = append(anagramsMap[key], str)
	}
	result := [][]string{}
	for _, group := range anagramsMap{
		result = append(result, group)
	}
	return result
}
'''
File: practice/groupAnagrams.go
'''

