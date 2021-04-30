import socket
import json

def send(client_socket, msg):
    client_socket.send((msg + "\n").encode())
    return 0

def recieve(client_socket):
    return client_socket.recv(4096).decode()

def main():
    isConOk = False
    while not isConOk:
        print("Enter ip:")
        ip = input()
        print("Enter port:")
        port = int(input())
        try:
            client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # socket.SOCK_STREAM
            client_socket.connect((ip, port))
            isConOk = True
        except socket.error:
            print("Error: host unreachable or incorrect IP, try again:")
            isConOk = False

    print("Received number of files:")
    strNOI = recieve(client_socket)
    print(strNOI)
    numOfIds = int(json.loads(strNOI)["message"])

    print("Enter id of file:")
    print(recieve(client_socket))

    id = input()
    while(int(id) <= 0 or int(id) > int(numOfIds)):#numOfIds
        print("Enter id of file:")
        id = input()
    print('Sending id of chosen file: {"type": 1, "message": "' + str(int(id) - 1) + '"}')
    send(client_socket,'{"type": 1, "message": "' + str(int(id) - 1) + '"}')
    f = open('file' + str(id), "w")
    blocks = recieve(client_socket)
    print("Received message with number of blocks", blocks)
    numOfBlocks = int(json.loads(blocks)["message"])
    print("Number of blocks: " + str(numOfBlocks) + "")
    print('Sending message to recieve file path: {"type": 3, "message": "' + str(int(id) - 1) + '"}')
    send(client_socket, '{"type": 3, "message": "' + str(int(id) - 1) + '"}')
    for i in range(0, numOfBlocks):
        block = recieve(client_socket)
        print("Received block: ", block)
        f.write(block)
    f.close()
    client_socket.close()

    f = open('file' + str(id), 'r')
    print("File: ")
    for line in f:
        print(line)

if __name__ == "__main__":
    main()

