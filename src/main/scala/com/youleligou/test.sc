



val pattern = """.*restaurant_id=(\d*)""".r
val pattern(nu) = "/shopping/v2/menu?restaurant_id=150880085"
nu.toLong