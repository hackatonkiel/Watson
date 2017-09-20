LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := BewatecCamTest
LOCAL_PRIVILEGED_MODULE := true

LOCAL_STATIC_JAVA_LIBRARIES := BewatecLibraryIPC websocketlib watsonlib


LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := websocketlib:libs/Java-WebSocket-1.3.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += watsonlib:libs/java-sdk-3.9.1-jar-with-dependencies.jar

include $(BUILD_MULTI_PREBUILT)
