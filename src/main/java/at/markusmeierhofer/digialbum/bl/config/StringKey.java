package at.markusmeierhofer.digialbum.bl.config;

public enum StringKey {
    MAIN_PAGE_DATA_FILE_NOT_FOUND_HEADER("mainPage.dataFileNotFoundHeader"),
    MAIN_PAGE_DATA_FILE_NOT_FOUND_TEXT("mainPage.dataFileNotFoundText"),
    MAIN_PAGE_IMAGE_VIEW_NO_MORE_PICTURES_AVAILABLE("mainPage.imageView.noMorePicturesAvailable"),
    MAIN_PAGE_IMAGE_VIEW_PICTURE_NOT_FOUND("mainPage.imageView.pictureNotFound"),
    MAIN_PAGE_SETTINGS_BTN("mainPage.settingsBtn"),
    MAIN_PAGE_BACK_BTN("mainPage.backBtn"),
    MAIN_PAGE_NEXT_BTN("mainPage.nextBtn"),
    MAIN_PAGE_CLOSE_BTN("mainPage.closeBtn"),
    SETTINGS_CREATOR_SETTINGS_FILE_NOT_FOUND_HEADER("settingsCreator.settingsFileNotFoundHeader"),
    SETTINGS_CREATOR_SETTINGS_FILE_NOT_FOUND_TEXT("settingsCreator.settingsFileNotFoundText"),
    DATA_PAGE_CHOOSE_IMAGE_BTN("dataPage.chooseImageBtn"),
    DATA_PAGE_SAVE_BTN("dataPage.saveBtn"),
    DATA_PAGE_HEADER_LBL("dataPage.headerLbl"),
    DATA_PAGE_IMAGE_URL_LBL("dataPage.imageUrlLbl"),
    DATA_PAGE_DESCRIPTION_LBL("dataPage.descriptionLbl"),
    DATA_PAGE_TITLE("dataPage.title"),
    ;

    private String key;

    StringKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
