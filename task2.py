import requests
from requests import api
from pygmaps import maps as pygmaps
def getAPI():
    response_stations = requests.get("https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json")
    var1 = response_stations.json()
    #test = var1['data']['stations'][0]
    #print(test['station_id'])
    response_stations_status = requests.get("https://gbfs.urbansharing.com/oslobysykkel.no/station_status.json")
    var2 = response_stations_status.json()
    process_data(var1, var2)

def process_data(data_1, data_2):
    #name
    #address
    #capacity
    #num_bikes_available
    #num_docks_available

    name = "empty"
    address = "empty"
    capacity = "empty"
    num_bikes_available = "empty"
    num_docks_available = "empty"
    station_id = "empty"

    for stations in data_1['data']['stations']:
        station_id = stations['station_id']
        name = stations['name']
        address = stations['address']
        capacity = stations['capacity']
        for stations_status in data_2['data']['stations']:
            if station_id == stations_status['station_id']:
                num_bikes_available = stations_status['num_bikes_available']
                num_docks_available = stations_status['num_docks_available']
                print_data(name, address, capacity, num_bikes_available, num_docks_available)
        if num_bikes_available == "empty":
            num_bikes_available = "zero"
        if num_docks_available == "empty":
            num_docks_available = "zero"
        if num_bikes_available == "zero" or num_docks_available == "zero":
            print_data(name, address, capacity, num_bikes_available, num_docks_available)
            

def print_data(name, address, cap, num_bikes_available, num_docks_available):
    print("Station: ", name)
    print("Address: ", address)
    print("Capacity: ", cap)
    print("Number of bikes available: ", num_bikes_available)
    print("Number of docks available: ", num_docks_available)
    print("\n\n")

def draw_map():
    #gmap = gmplot.GoogleMapPlotter.from_geocode( "Oslo, Norway") 
    gmap1 = pygmaps.maps(30.3164945, 
                                78.03219179999999, 13) 
    #gmap.draw("/Users/abenayan/Desktop/Ardoq/map1.html")
    gmap1.draw( "map1.html" ) 

if __name__ == "__main__":
    #getAPI()
    draw_map()