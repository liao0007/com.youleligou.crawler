```
PUT eleme-restaurant
{
    "mappings": {
        "snapshot": {
            "properties": {
          "address": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "averageCost": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "createdAt": {
            "type": "date"
          },
          "createdDate": {
            "type": "date"
          },
          "deliveryFee": {
            "type": "float"
          },
          "description": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "id": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "identification": {
            "type": "object"
          },
          "imagePath": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "isNew": {
            "type": "boolean"
          },
          "isPremium": {
            "type": "boolean"
          },
          "location": {
	            "type": "geo_point"
	        },
          "minimumOrderAmount": {
            "type": "float"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "phone": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "promotionInfo": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "rating": {
            "type": "float"
          },
          "ratingCount": {
            "type": "long"
          },
          "recentOrderNum": {
            "type": "long"
          },
          "restaurantId": {
            "type": "long"
          },
          "status": {
            "type": "long"
          }
        }
        }
    },
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    }
}

PUT eleme-food
{
    "mappings": {
        "snapshot": {
            "properties": {
          "balancedPrice": {
            "type": "float"
          },
          "category": {
            "properties": {
              "categoryId": {
                "type": "long"
              },
              "createdAt": {
                "type": "date"
              },
              "description": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "iconUrl": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "isActivity": {
                "type": "boolean"
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "restaurantId": {
                "type": "long"
              },
              "typ": {
                "type": "long"
              }
            }
          },
          "categoryId": {
            "type": "long"
          },
          "createdAt": {
            "type": "date"
          },
          "createdDate": {
            "type": "date"
          },
          "description": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "foodSkus": {
          "type": "nested",
            "properties": {
              "checkoutMode": {
                "type": "long"
              },
              "foodId": {
                "type": "long"
              },
              "isEssential": {
                "type": "boolean"
              },
              "itemId": {
                "type": "long"
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "originalPrice": {
                "type": "float"
              },
              "packingFee": {
                "type": "float"
              },
              "price": {
                "type": "float"
              },
              "promotionStock": {
                "type": "long"
              },
              "recentPopularity": {
                "type": "long"
              },
              "recentRating": {
                "type": "float"
              },
              "restaurantId": {
                "type": "long"
              },
              "skuId": {
                "type": "long"
              },
              "soldOut": {
                "type": "boolean"
              },
              "stock": {
                "type": "long"
              }
            }
          },
          "id": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "itemId": {
            "type": "long"
          },
          "monthRevenue": {
            "type": "float"
          },
          "monthSales": {
            "type": "long"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "rating": {
            "type": "float"
          },
          "ratingCount": {
            "type": "long"
          },
          "restaurant": {
            "properties": {
              "address": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "createdAt": {
                "type": "date"
              },
              "identification": {
                "type": "object"
              },
              "imagePath": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "location": {
              "type": "geo_point"
          },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "restaurantId": {
                "type": "long"
              }
            }
          },
          "restaurantId": {
            "type": "long"
          },
          "satisfyCount": {
            "type": "long"
          },
          "satisfyRate": {
            "type": "float"
          }
        }
        }
    },
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    }
}
```

meituan
```
PUT meituan-poi
{
    "mappings": {
        "snapshot": {
            "properties": {
          "avgDeliveryTime": {
            "type": "long"
          },
          "brandType": {
            "type": "long"
          },
          "createdAt": {
            "type": "date"
          },
          "createdDate": {
            "type": "date"
          },
          "id": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "location": {
              "type": "geo_point"
          },
          "minPrice": {
            "type": "float"
          },
          "minPriceTip": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "monthSaleNum": {
            "type": "long"
          },
          "mtPoiId": {
            "type": "long"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "picUrl": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "poiId": {
            "type": "long"
          },
          "sales": {
            "type": "long"
          },
          "shippingFee": {
            "type": "float"
          },
          "shippingFeeTip": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "status": {
            "type": "long"
          },
          "wmPoiOpeningDays": {
            "type": "long"
          },
          "wmPoiViewId": {
            "type": "long"
          }
        }
        }
    },
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    }
}

PUT meituan-spu
{
    "mappings": {
        "snapshot": {
            "properties": {
          "balancedPrice": {
            "type": "float"
          },
          "createdAt": {
            "type": "date"
          },
          "createdDate": {
            "type": "date"
          },
          "description": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "foodTag": {
            "properties": {
              "createdAt": {
                "type": "date"
              },
              "icon": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "poiId": {
                "type": "long"
              },
              "tagId": {
                "type": "long"
              },
              "typ": {
                "type": "long"
              }
            }
          },
          "id": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "minPrice": {
            "type": "float"
          },
          "monthRevenue": {
            "type": "float"
          },
          "monthSaled": {
            "type": "long"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "picture": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "poi": {
            "properties": {
              "brandType": {
                "type": "long"
              },
              "createdAt": {
                "type": "date"
              },
              "location": {
              "type": "geo_point"
          },
              "mtPoiId": {
                "type": "long"
              },
              "name": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "picUrl": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "poiId": {
                "type": "long"
              },
              "status": {
                "type": "long"
              },
              "wmPoiViewId": {
                "type": "long"
              }
            }
          },
          "poiId": {
            "type": "long"
          },
          "praiseNum": {
            "type": "long"
          },
          "praiseNumNew": {
            "type": "long"
          },
          "skus": {
          "type":"nested",
            "properties": {
              "activityStock": {
                "type": "long"
              },
              "boxNum": {
                "type": "float"
              },
              "boxPrice": {
                "type": "float"
              },
              "description": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "id": {
                "type": "long"
              },
              "minOrderCount": {
                "type": "long"
              },
              "originPrice": {
                "type": "float"
              },
              "picture": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "price": {
                "type": "float"
              },
              "promotionInfo": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "realStock": {
                "type": "long"
              },
              "restrict": {
                "type": "long"
              },
              "spec": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "status": {
                "type": "long"
              },
              "stock": {
                "type": "long"
              }
            }
          },
          "spuId": {
            "type": "long"
          },
          "status": {
            "type": "long"
          },
          "tagId": {
            "type": "long"
          },
          "treadNum": {
            "type": "long"
          }
        }
        }
    },
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    }
}
```

crawler
```
PUT crawler
{
    "mappings": {
        "job": {
            "properties": {
          "completedAt": {
            "type": "date"
          },
          "createdAt": {
            "type": "date"
          },
          "id": {
            "type": "text"
          },
          "jobName": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "jobType": {
            "type": "text"
          },
          "statusCode": {
            "type": "long"
          },
          "statusMessage": {
            "type": "text"
          },
          "url": {
            "type": "text"
          },
          "useProxy": {
            "type": "boolean"
          }
        }
        }
    },
    "settings": {
        "index": {
            "number_of_shards": "3",
            "number_of_replicas": "1"
        }
    }
}
```