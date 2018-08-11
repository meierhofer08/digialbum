//package at.markusmeierhofer.digialbum;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class PackageConverter {
//    public static void main(String[] args) {
//        VTDDataAccess dataAccess = VTDDataAccess.getInstance();
//        List<sample.VTDEntry> oldEntries = dataAccess.loadData();
//        List<VTDEntry> newEntries = oldEntries.stream()
//                .map(vtdEntry -> new VTDEntry(vtdEntry.getHeader().get(), vtdEntry.getImageUrl().get(), vtdEntry.getText().get()))
//                .collect(Collectors.toList());
//        dataAccess.saveData(newEntries);
//    }
//}
