package com.candykick.cdkgallery;

import java.io.File;

/**
 * Created by candykick on 2019. 9. 21..
 *
 * 미디어 스캔을 통해 저장된 이미지 파일을 기본 갤러리로도 확인할 수 있게 하기 위한 Interface.
 * 호출 주체가 불확실하다. (GalleryActivity에서 호출해야 하나???)
 */

public interface CdkCameraInterface {
    void onSave(File filePath);
}
