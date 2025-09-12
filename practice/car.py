class Car:
    # Class variable (shared by all instances)
    wheels = 4

    # Constructor
    def __init__(self, brand, model, year):
        self.brand = brand
        self.model = model
        self.year = year
        self.speed = 0

    # Instance Method (works with object data)
    def display_info(self):
        return f"{self.year} {self.brand} {self.model} ({self.speed} km/h)"

    def accelerate(self, amount):
        self.speed += amount
        return f"Accelerated to {self.speed} km/h"

    def brake(self, amount):
        self.speed = max(0, self.speed - amount)
        return f"Braked to {self.speed} km/h"

    # Class Method (works with class-level data, not instance)
    @classmethod
    def change_wheels(cls, count):
        cls.wheels = count
        return f"All cars now have {cls.wheels} wheels"

    # Static Method (utility function, doesn’t need instance or class)
    @staticmethod
    def honk():
        return "Beep! 🚗"

# Create two cars
car1 = Car("Tesla", "Model S", 2025)
car2 = Car("BMW", "i8", 2024)

# Instance methods
print(car1.display_info())        # 2025 Tesla Model S (0 km/h)
print(car1.accelerate(50))        # Accelerated to 50 km/h

# Class method (affects all cars)
print(Car.change_wheels(6))       # All cars now have 6 wheels
print(car1.wheels, car2.wheels)   # 6 6

# Static method
print(Car.honk())                 # Beep! 🚗
