package com.xebia.bpt

import org.scalatra._

trait BptScalaStack extends ScalatraServlet {

  notFound {
    response.setStatus(404)
  }
}
