/* Group Anagrams
Given an array of strings, group anagrams together.
*/
package main

import (
	"sort"
	"strings"
	"fmt"
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

func main() {
	strs := []string{"eat", "tea", "tan", "ate", "nat", "bat"}
	anagrams := groupAnagrams(strs)
	for _, group := range anagrams {
		fmt.Println(group)
	}

}

