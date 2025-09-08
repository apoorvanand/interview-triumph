import json
from typing import Any, Dict


def json_dumps(data: Dict[str, Any]) -> str:
    """Custom implementation of json.dumps for dicts."""
    if not isinstance(data, dict):
        raise TypeError("Input must be a dictionary")
    return "{" + ", ".join(
        f'"{str(k)}": {encode_value(v)}'
        for k, v in data.items()
    ) + "}"


def encode_value(value: Any) -> str:
    """Helper: encode Python objects to JSON-compatible strings."""
    if isinstance(value, str):
        return f'"{value}"'
    elif isinstance(value, (int, float, bool)) or value is None:
        return json.dumps(value)  # handles True, False, None, numbers
    elif isinstance(value, dict):
        return json_dumps(value)
    elif isinstance(value, list):
        return "[" + ", ".join(encode_value(v) for v in value) + "]"
    else:
        raise TypeError(f"Unsupported type: {type(value)}")


def json_loads(data: str) -> Dict[str, Any]:
    """Custom implementation of json.loads for dicts."""
    try:
        parsed = json.loads(data)  # use Python's parser
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
    encoded = json_dumps(original_dict)
    print("Custom json_dumps:", encoded)

    # Decode
    decoded = json_loads(encoded)
    print("Custom json_loads:", decoded)
