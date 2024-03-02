package main

import (
	"crypto/md5"
	"encoding/hex"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/golang/glog"
)

const ttsHttpUrl = "https://open.mobvoi.com/api/tts/v1"

// appKey和appSecret从https://openapi.mobvoi.com/user/mine-app-list创建应用获取
var appKey = ""
var appSecret = ""

var ttsPostData = map[string]interface{}{
	"signature":    "appkey+secret+timestamp的MD5值",
	"timestamp":    "",
	"appkey":       appKey,
	"speaker":      "cissy_meet",
	"ignore_limit": true,
	"gen_srt":      true,
	"audio_type":   "mp3",
	"text":         "海南长臂猿的叫声，高亢洪亮，响彻山谷。海南热带雨林国家公园是这种濒危灵长类动物的全球唯一栖息地。经过近年来的科学保护和生态恢复，海南长臂猿已由最少时的寥寥几只，恢复到5群35只，创造了世界珍稀动物保护的奇迹。国家公>园堪称最美国土，具有典型独特的自然生态系统、世界瞩目的野生动植物种。在海南热带雨林国家公园，这里生长着846种特有植物、145种国家重点保护野生动物，生物多样性指数与巴西亚马孙雨林相当。2018年4月，习近平总书记在庆祝海>南建省办经济特区30周年大会上强调，要积极开展国家公园体制试点，建设热带雨林等国家公园。2019年1月，总书记又主持召开中央全面深化改革委员会第六次会议，审议通过《海南热带雨林国家公园体制试点方案》。被称为海南“生态绿心”的这片最美国土迈出保护和建设的历史性一步。我国的国家公园在自然保护地体系中保护等级最高、生态价值最大、管控措施最严。",
	"speed":        "1.1",
}

func genMD5Sum(s string) string {
	glog.Info("genMD5Sum: ", s)
	md5_hash := md5.New()
	md5_hash.Write([]byte(s))
	ret := md5_hash.Sum(nil)
	glog.V(1).Info("genMD5Sum bytes: ", ret)
	rets := hex.EncodeToString(ret)
	glog.Info("genMD5Sum: ", rets)
	return rets
}

func getTimeNow() int64 {
	now := time.Now()
	return now.Unix()
}

func ttsSample() {
	ttsPostData["timestamp"] = fmt.Sprintf("%v", getTimeNow())
	ttsPostData["signature"] = genMD5Sum(fmt.Sprintf("%v+%v+%v", appKey, appSecret, ttsPostData["timestamp"]))
	// convert ttsPostData to String
	ttsPostByte, err := json.Marshal(ttsPostData)
	if err != nil {
		glog.Error(err)
		return
	}

	ttsPostStr := string(ttsPostByte)
	glog.Info("post data: " + ttsPostStr)

	payload := strings.NewReader(ttsPostStr)
	method := "POST"
	client := &http.Client{}
	req, err := http.NewRequest(method, ttsHttpUrl, payload)

	if err != nil {
		glog.Error(err)
		return
	}
	req.Header.Add("Content-Type", "application/json")

	res, err := client.Do(req)
	if err != nil {
		glog.Error(err)
		return
	}
	glog.Info(res)
	if res.StatusCode != 200 {
		glog.Error("response code: ", res.StatusCode)
		return
	}
	if res.Header.Get("Content-Type") != "audio/mpeg" {
		glog.Warning("Content-Type: ", res.Header.Get("Content-Type"))
		b, _ := io.ReadAll(res.Body)
		glog.Warning("Response body: ", string(b))
		return
	}
	defer res.Body.Close()

	out, err := os.Create("demo.mp3")
	if err != nil {
		panic(err)
	}
	defer out.Close()

	_, err = io.Copy(out, res.Body)
	if err != nil {
		panic(err)
	}
}

func main() {
	flag.Set("logtostderr", "true")
	flag.Set("v", "2")
	flag.Set("stderrthreshold", "INFO")
	ttsSample()
}
