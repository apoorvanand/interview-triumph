def infix_to_postfix(expression:str) -> str:
    precedence = {'+':1, '-':1, '*':2,'/':2,'^':3}
    stack = []
    output = []
    for char in expression:
        if char.isalnum():  # If the character is an operand, add it to output
            output.append(char)
        elif char == '(':  # If the character is '(', push it to stack
            stack.append(char)
        elif char == ')':  # If the character is ')', pop and output from the stack until an '(' is encountered
            while stack and stack[-1] != '(':
                output.append(stack.pop())
            stack.pop()  # Pop '(' from the stack
        else:  # An operator is encountered
            while (stack and stack[-1] != '(' and
                   precedence[char] <= precedence.get(stack[-1], 0)):
                output.append(stack.pop())
            stack.append(char)
    while stack:  # Pop all the operators from the stack
        output.append(stack.pop())
    return ''.join(output)
def postfix_to_infix(expression: str) -> str:
    stack = []
    for char in expression:
        if char.isalnum():  # If the character is an operand, push it to stack
            stack.append(char)
        else:  # The character is an operator
            right = stack.pop()
            left = stack.pop()
            new_expr = f"({left}{char}{right})"
            stack.append(new_expr)
    return stack[-1]  # The final element in the stack is the complete infix expression
if __name__ == "__main__": 
    infix_expr = "a+b*(c^d-e)^(f+g*h)-i"
    postfix_expr = infix_to_postfix(infix_expr)
    print(f"Infix: {infix_expr} -> Postfix: {postfix_expr}")
    restored_infix = postfix_to_infix(postfix_expr)
    print(f"Postfix: {postfix_expr} -> Infix: {restored_infix}")