// Copyright 2024 Mobvoi Inc. All Rights Reserved.
// Author: easytojoin@163.com (fei.hao)

#include <time.h>

#include <fstream>

#include "curl/curl.h"
#include "glog/logging.h"
#include "nlohmann/json.hpp"
#include "openssl/evp.h"


constexpr char kTtsUrl[] = "https://open.mobvoi.com/api/tts/v1";
// AppKey和AppSecret从https://openapi.mobvoi.com/user/mine-app-list创建应用获取
constexpr char kAppKey[] = "";
constexpr char kAppSecret[] = "";

class TtsDemo {
 public:
  TtsDemo();
  ~TtsDemo();
  void Run();
 private:
  std::size_t CallBack() {
    return -1;
  }
  void Synthesis();
  void CleanUp();
  std::string GetPostData();
  std::string GenMd5Sum(const std::string& text);
 private:
  std::string tts_url_;
  CURL* tts_handler_;
};

TtsDemo::TtsDemo() {
  LOG(INFO) << "Initialize";
  tts_url_ = std::string(kTtsUrl);
  tts_handler_ = curl_easy_init();
}

TtsDemo::~TtsDemo() {
  LOG(INFO) << "Destroyed";
  CleanUp();
}

void TtsDemo::Run() {
  LOG(INFO) << "TtsDemo Running";
  Synthesis();
}

#include <stdlib.h> /* for realloc */
#include <string.h> /* for memcpy */

struct memory {
  char *response;
  size_t size;
};
 
static size_t cb(void *data, size_t size, size_t nmemb, void *clientp)
{
  size_t realsize = size * nmemb;
  struct memory *mem = (struct memory *)clientp;
 
  char *ptr = (char*)realloc(mem->response, mem->size + realsize + 1);
  if(!ptr)
    return 0;  /* out of memory! */
 
  mem->response = ptr;
  memcpy(&(mem->response[mem->size]), data, realsize);
  mem->size += realsize;
  mem->response[mem->size] = 0;
  LOG(INFO) << mem->response;
  return realsize;
}

void TtsDemo::Synthesis() {
  curl_easy_setopt(tts_handler_, CURLOPT_URL, tts_url_.c_str());

  // set headers
  struct curl_slist* headers = nullptr;
  headers = curl_slist_append(headers, "Content-Type:application/json");
  curl_easy_setopt(tts_handler_, CURLOPT_HTTPHEADER, headers);

  // set post data
  std::string post_data = this->GetPostData();
  LOG(INFO) << "post: " << post_data;
  curl_easy_setopt(tts_handler_, CURLOPT_POSTFIELDS, post_data.c_str());
  curl_easy_setopt(tts_handler_, CURLOPT_POSTFIELDSIZE, post_data.length());

  struct memory chunk = {0};
  curl_easy_setopt(tts_handler_, CURLOPT_WRITEFUNCTION, cb);
  curl_easy_setopt(tts_handler_, CURLOPT_WRITEDATA, (void *)&chunk);
  // http post
  CURLcode res = curl_easy_perform(tts_handler_);
  long status_code = 0;
  curl_easy_getinfo(tts_handler_, CURLINFO_RESPONSE_CODE, &status_code);
  if (status_code != 200) {
    LOG(ERROR) << "Bad status: " << status_code;
  }
  struct curl_header *header;
  CURLHcode h = curl_easy_header(tts_handler_, "Content-Type", 0, CURLH_HEADER, -1, &header);
  if (h != CURLHcode::CURLHE_OK) {
    LOG(ERROR) << "Get Response header failed " << h;
  }
  LOG(INFO) << header->name << ":" << header->value;

  if (header->value != "audio/mpeg") {
    LOG(ERROR) << chunk.response;
    return;
  }
  std::ofstream audio_file;
  audio_file.open("./tts.wav");
  std::string audio_data(chunk.response, chunk.size);
  audio_file << audio_data;
}

void TtsDemo::CleanUp() {
  if (tts_handler_) {
    curl_easy_cleanup(tts_handler_);
    tts_handler_ = nullptr;
  }
}

std::string TtsDemo::GetPostData() {
  nlohmann::json post_param;
  post_param["text"] = "出门问问成立于2012年，是一家以语音交互和软硬结合为核心的人工智能公司，为全球40多个国家和地区的消费者、企业提供人工智能产品和服务。";
  post_param["speaker"] = "xiaoyi_meet";
  post_param["audio_type"] = "mp3";
  post_param["speed"] = 1.0;
  post_param["gen_srt"] = false;
  post_param["appkey"] = kAppKey;
  std::string timestamp = [](){
    time_t now_time = time(NULL);
    return std::to_string(now_time);
  }();
  post_param["timestamp"] = timestamp;
  post_param["signature"] = this->GenMd5Sum(std::string(kAppKey).append("+") +
                                        std::string(kAppSecret).append("+") +
                                        timestamp);
  return post_param.dump();
}

std::string TtsDemo::GenMd5Sum(const std::string& text) {
  EVP_MD_CTX* context = EVP_MD_CTX_new();
  const EVP_MD* md = EVP_md5();
  unsigned char md_value[EVP_MAX_MD_SIZE];
  unsigned int  md_len;
  std::string  output;
  EVP_DigestInit_ex2(context, md, NULL);
  EVP_DigestUpdate(context, text.c_str(), text.length());
  EVP_DigestFinal_ex(context, md_value, &md_len);
  EVP_MD_CTX_free(context);

  output.resize(md_len * 2);
  for (unsigned int i = 0 ; i < md_len ; ++i)
    std::sprintf(&output[i * 2], "%02x", md_value[i]);
  LOG(INFO) << "Get md5 of " << text << " is " << output;
  return output;
}

int main(int argc, char** argv) {
  TtsDemo demo;
  demo.Run();
  return 0;
}
