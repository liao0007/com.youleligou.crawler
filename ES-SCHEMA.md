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
                    "type": "text"
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
                    "type": "text"
                },
                "identification": {
                    "properties": {
                        "companyName": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword",
                                    "ignore_above": 256
                                }
                            }
                        },
                        "licensesNumber": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword",
                                    "ignore_above": 256
                                }
                            }
                        }
                    }
                },
                "imagePath": {
                    "type": "text"
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
                    "type": "text"
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
                    "type": "integer"
                },
                "recentOrderNum": {
                    "type": "integer"
                },
                "restaurantId": {
                    "type": "long"
                },
                "status": {
                    "type": "integer"
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
                        "activity": {
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
                            "type": "text"
                        },
                        "id": {
                            "type": "long"
                        },
                         "restaurantId": {
                            "type": "long"
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
                        "typ": {
                            "type": "integer"
                        }
                    }
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
                    "type": "text"
                },
                "itemId": {
                    "type": "long"
                },
                "monthSales": {
                    "type": "integer"
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
                    "type": "integer"
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
                        "id": {
                            "type": "long"
                        },
                        "identification": {
                            "properties": {
                                "companyName": {
                                    "type": "text",
                                    "fields": {
                                        "keyword": {
                                            "type": "keyword",
                                            "ignore_above": 256
                                        }
                                    }
                                },
                                "licensesNumber": {
                                    "type": "text",
                                    "fields": {
                                        "keyword": {
                                            "type": "keyword",
                                            "ignore_above": 256
                                        }
                                    }
                                }
                            }
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
                        }
                    }
                },
                "satisfyCount": {
                    "type": "integer"
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