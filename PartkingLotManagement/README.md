Parking Lot Management :

APIs:

/parkinglot -- Used for creating a Parking Lot.
/parkinglot/{parkingLotId}/floor -- Used for creating a floor for a lot.
/parkinglot/{parkingLotId}/floor/{floorId}/bay -- Used for creating a bay with different size for the floor.
/getslot/{parkingLotId}/{size} -- Allocate slot for a car.
/releaseslot/{parkingLotId}/{slotId} -- Releases an allocated slot.

Data Model:

Mysql is used for storing the parking lot information, floor per parking lot, and allocated and free bays per floor.
