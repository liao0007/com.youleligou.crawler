"""(.*):([0-9]+)@(.*)#(.*)"""
  .r
  .findAllIn("218.21.169.19:8998@HTTP#内蒙古包头市 联通")
  .matchData
  .map(_.toString())