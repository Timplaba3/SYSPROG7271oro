import socket
import json

def send(client_socket, msg, dst):
    client_socket.sendto((msg + "\n").encode(), dst)
    return 0

def recieve(client_socket):
    data, address = client_socket.recvfrom(1024)
    return data.decode()

def main():

    isConOk = False
    while not isConOk:
        print("Enter ip:")
        ip = input()
        print("Enter port:")
        port = int(input())
        try:
            client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)#socket.SOCK_STREAM
            isConOk = True
        except socket.error:
            print("Error: host unreachable or incorrect IP, try again:")
            isConOk = False

    dest = (ip, port)
    print("Sending client hello: " + '{"type": 0, "message": ""}' + " to server: " + ip + ":" + str(port))
    send(client_socket, '{"type": 0, "message": ""}', dest)
    numOfFiles = recieve(client_socket)
    print("Num of files: " + numOfFiles)
    print("Recieved file list:")
    print(recieve(client_socket))
    print("Enter id of file:")

    id = input()
    while(int(id) <= 0 or int(id) > int(numOfFiles)):#numOfIds
        print("Enter id of file:")
        id = input()
    print('Sending id of chosen file: {"type": 1, "message": "' + str(int(id) - 1) + '"}')
    send(client_socket,'{"type": 1, "message": "' + str(int(id) - 1) + '"}', dest)

    fileSizeMessage = recieve(client_socket)
    print("File size messqge: " + fileSizeMessage)
    fileSize = int(json.loads(fileSizeMessage)["message"])
    print("Filesize: ", fileSize)
    print('Sending message to recieve file path: {"type": 3, "message": "' + str(int(id) - 1) + '"}')
    send(client_socket, '{"type": 3, "message": "' + str(int(id) - 1) + '"}', dest)
    fullname = recieve(client_socket)
    print("Fullname: "+fullname+", file size: "+str(fileSize)+" byte")
    client_socket.close()

if __name__ == "__main__":
    main()

