from typing import Any, Dict

class Config:
    def __init__(self) -> None:
        self.grpc_server_address: str = "localhost:50051"
        self.http_port: int = 8080
        self.timeout: int = 5  # seconds

    def to_dict(self) -> Dict[str, Any]:
        return {
            "grpc_server_address": self.grpc_server_address,
            "http_port": self.http_port,
            "timeout": self.timeout,
        }