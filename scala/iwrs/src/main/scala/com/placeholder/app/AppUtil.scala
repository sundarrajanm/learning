package com.placeholder.app


object AppUtil {
  def firstStep(app: IWRSApplication) =
    app.steps find (s => s.step equalsIgnoreCase "start")
}
