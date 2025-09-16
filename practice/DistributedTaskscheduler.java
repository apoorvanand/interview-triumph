*/
DistributedTaskScheduler.java

*/
package practice;
import java.util.*;
public class DistributedTaskScheduler {
    public int leastInterval(char[] tasks, int n) {
        Map<Character, Integer> tasksCount = new HashMap<>();
        // Since tasks are uppercase English letters, an array is more efficient.
        int[] taskCounts = new int[26];
        for (char task : tasks) {
            taskCounts[task - 'A']++;
        }

        // Find the frequency of the most frequent task.
        int maxFreq = 0;
        for (int count : taskCounts) {
            maxFreq = Math.max(maxFreq, count);
        }

        // Count how many tasks have this maximum frequency.
        int maxFreqCount = 0;
        for (int count : taskCounts) {
            if (count == maxFreq) {
                maxFreqCount++;
            }
        }

        // Calculate the minimum time using a formula.
        // The structure is determined by the most frequent task.
        // There will be (maxFreq - 1) blocks of tasks, each of size (n + 1).
        // The final "block" consists of the number of tasks that share the max frequency.
        // Example: A, B, idle, A, B, idle, A, B
        // Here, maxFreq=3, n=2. We have (3-1) blocks of size (2+1). The last block has 2 tasks (A,B).
        int time = (maxFreq - 1) * (n + 1) + maxFreqCount;

        // The total time cannot be less than the total number of tasks (this handles cases where n=0 or no idle time is needed).
        return Math.max(tasks.length, time);
    }
}