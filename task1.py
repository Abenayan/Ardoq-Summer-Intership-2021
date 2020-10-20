from random import randint
def function(array):
    product = 1
    if len(array) < 3:
        #If list is less than 3, for now return -1
        return "Error"
    
    #If excatly 3, no need to go through list
    if len(array) == 3:
        for n in array:
            product *= n
        return product   
    
    #If more than 3 elements, sort array and get head/tail elements to find product
    array = quicksort(array)
    product = array[-1] * array[-2] * array[-3]
    #In cases there are two negative elements that have a higher product
    tempproduct = array[-1] * array[1] * array[0]
    print(array)
    if tempproduct > product:
        return tempproduct
    return product


def quicksort(array):
    if len(array) < 2:
        return array

    low, same, high = [], [], []
    pivot = array[randint(0, len(array) - 1)]

    for n in array:
        if n < pivot:
            low.append(n)
        elif n == pivot:
            same.append(n)
        elif n > pivot:
            high.append(n)
    
    return quicksort(low) + same + quicksort(high)


def test():
    #Base tests - simple code

    #If test nr1
    print(function([1,2]))
    #If test nr2
    print(function([1,2,3]))
    #With negative number
    print(function([42,56,1,67,3,7,9,-102,-4,-60]))
    print(function([35,24,64,2,3,45,57,96,875,2467,4,-1537,1-15,14,-113]))

    


if __name__ == "__main__":
    a = [1, 10, 2, 6, 5, 3]
    print(function(a))
    
    #Test
    test()