'''
Implement json encoder and decoder for dict data structure

'''
import json
from typing import Any, Dict


class DictJSONEncoder:
    """Custom JSON encoder for dict objects."""

    @staticmethod
    def encode(data: Dict[str, Any]) -> str:
        if not isinstance(data, dict):
            raise TypeError("Input must be a dictionary")
        return "{" + ", ".join(
            f'"{str(k)}": {DictJSONEncoder._encode_value(v)}'
            for k, v in data.items()
        ) + "}"

    @staticmethod
    def _encode_value(value: Any) -> str:
        if isinstance(value, str):
            return f'"{value}"'
        elif isinstance(value, (int, float, bool)) or value is None:
            return json.dumps(value)  # handles True/False/None properly
        elif isinstance(value, dict):
            return DictJSONEncoder.encode(value)
        elif isinstance(value, list):
            return "[" + ", ".join(DictJSONEncoder._encode_value(v) for v in value) + "]"
        else:
            raise TypeError(f"Unsupported type: {type(value)}")


class DictJSONDecoder:
    """Custom JSON decoder for dict objects."""

    @staticmethod
    def decode(data: str) -> Dict[str, Any]:
        try:
            parsed = json.loads(data)
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON: {e}")

        if not isinstance(parsed, dict):
            raise TypeError("Decoded JSON is not a dictionary")
        return parsed


# Example usage
if __name__ == "__main__":
    original_dict = {
        "name": "Alice",
        "age": 30,
        "is_member": True,
        "balance": None,
        "skills": ["Python", "ML", "Finance"],
        "nested": {"a": 1, "b": 2}
    }

    # Encode
    encoded = DictJSONEncoder.encode(original_dict)
    print("Encoded JSON:", encoded)

    # Decode
    decoded = DictJSONDecoder.decode(encoded)
    print("Decoded dict:", decoded)
