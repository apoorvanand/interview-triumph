import unittest
from compress_decompress import compress_rle, decompress_rle

class TestRLECompression(unittest.TestCase):

    def test_compress_empty_string(self):
        """Test compression of an empty string."""
        self.assertEqual(compress_rle(""), "")

    def test_compress_no_repeats(self):
        """Test compression of a string with no repeating characters."""
        self.assertEqual(compress_rle("ABC"), "1A1B1C")

    def test_compress_all_repeats(self):
        """Test compression of a string with all characters the same."""
        self.assertEqual(compress_rle("AAAAA"), "5A")

    def test_compress_mixed_string(self):
        """Test compression of a typical string with mixed characters."""
        self.assertEqual(compress_rle("AABBBCCCC"), "2A3B4C")

    def test_compress_long_run(self):
        """Test compression with a run longer than 9 characters."""
        self.assertEqual(compress_rle("AAAAAAAAAAAA"), "12A")

    def test_decompress_empty_string(self):
        """Test decompression of an empty string."""
        self.assertEqual(decompress_rle(""), "")

    def test_decompress_simple(self):
        """Test decompression of a simple compressed string."""
        self.assertEqual(decompress_rle("2A3B1C"), "AABBBC")

    def test_decompress_long_run(self):
        """Test decompression with a count greater than 9."""
        self.assertEqual(decompress_rle("12A"), "AAAAAAAAAAAA")

    def test_round_trip_simple(self):
        """Test that decompressing a compressed string yields the original."""
        original = "WWWWBBBAAAWWWWWW"
        compressed = compress_rle(original)
        decompressed = decompress_rle(compressed)
        self.assertEqual(original, decompressed)

    def test_round_trip_no_repeats(self):
        """Test round trip for a string with no repeating characters."""
        original = "PYTHON"
        compressed = compress_rle(original)
        decompressed = decompress_rle(compressed)
        self.assertEqual(original, decompressed)

    def test_limitation_with_digits(self):
        """
        Test the known limitation: the algorithm does not correctly handle
        original strings that contain digits.
        """
        original_with_digits = "A11B"
        compressed = compress_rle(original_with_digits) # -> "1A211B"
        decompressed = decompress_rle(compressed) # -> "A111111111111111111111B"
        self.assertNotEqual(original_with_digits, decompressed)

if __name__ == '__main__':
    unittest.main()
