# OpenApi demo for C++

## How to build?

1. Download vcpkg tool from <https://vcpkg.io/en/>
2. Install dependencies by vcpkg

   ```bash
   vcpkg install glog
   vcpkg install openssl
   vcpkg install curl
   vcpkg install nlohmann-json
   ```

3. Integrate vcpkg to cmake

   ```bash
   vcpkg integrate install
   ```

4. Specify vcpkg.cmake when you build

    ```bash
    cmake -B build -D -DCMAKE_TOOLCHAIN_FILE=${vcpkg_path}/scripts/buildsystems/vcpkg.cmake
    ```
