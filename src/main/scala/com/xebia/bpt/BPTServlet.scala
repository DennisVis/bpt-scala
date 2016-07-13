package com.xebia.bpt

class BPTServlet extends BptScalaStack {

  get("/") {
    status = 200
    contentType = "application/json"
    """{"version": "1.0.0"}"""
  }
}
