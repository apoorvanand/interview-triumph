class Car:
    
    def __init__(self, make: str, model: str, year: int):
        self.make = make
        self.model = model
        self.year = year
        self.speed = 0  # Initial speed is 0

    def accelerate(self, increment: int) -> None:
        self.speed += increment
        print(f"The car has accelerated. Current speed: {self.speed} km/h")
    def brake(self, decrement: int) -> None:
        self.speed = max(0, self.speed - decrement)  # Speed cannot be negative
        print(f"The car has braked. Current speed: {self.speed} km/h")
    def honk(self) -> None:
        print("Beep beep!")
    def get_info(self) -> str:
        return f"{self.year} {self.make} {self.model}, Speed: {self.speed} km/h"

if __name__ == "__main__":
    my_car = Car("Toyota", "Corolla", 2020)
    print(my_car.get_info())
    my_car.accelerate(30)
    my_car.honk()
    my_car.brake(10)
    print(my_car.get_info())
    my_car.brake(25)
    print(my_car.get_info())
