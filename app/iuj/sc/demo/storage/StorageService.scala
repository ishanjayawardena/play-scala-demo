package iuj.sc.demo.storage

import java.io.File
import java.net.URI

trait StorageService {
  def store(file: File, parameters: StorageParameters): URI
  def move(file: File, parameters: StorageParameters)
  def delete(file: File, parameters: StorageParameters)
}
