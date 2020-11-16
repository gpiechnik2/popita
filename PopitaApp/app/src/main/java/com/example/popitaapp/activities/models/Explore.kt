package com.example.popitaapp.activities.models

data class Explore(
        val id: Int,
        val user: User,
        val longitude: Float,
        val latitude: Float,
        val attitude: Float,
        val location: String,
        val timestamp: String,
        val distance: Float
)

//{
//    "count": 2,
//    "next": null,
//    "previous": null,
//    "results": [
//        {
//            "id": 1,
//            "user": {
//                "id": 1,
//                "first_name": "Root name"
//            },
//            "longitude": 50.045062,
//            "latitude": 22.003297,
//            "attitude": 1.0,
//            "location": "Sienkiewicza, Blabla",
//            "timestamp": "2020-11-16 07:11",
//            "distance": 0.19469918295969915
//        },
//        {
//            "id": 2,
//            "user": {
//                "id": 2,
//                "first_name": "greg1"
//            },
//            "longitude": 50.044945,
//            "latitude": 22.001849,
//            "attitude": 1.0,
//            "location": "Kochanowskiego, obok Sienkiewicza",
//            "timestamp": "2020-11-16 07:11",
//            "distance": 0.3546432935887253
//        }
//    ]
//}