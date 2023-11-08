import "./App.css";
import MapBox from "./MapBox";
import InputBox from "./components/SearhKeyword";
import { useState } from "react";

function App() {
  const [searchOverlay, setSearchOverlay] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  const handleSetSearchOverlay = (
    data: GeoJSON.FeatureCollection | undefined
  ) => {
    setSearchOverlay(data);
  };

  return (
    <div className="App">
      <MapBox />
      <div>
        <InputBox setState={handleSetSearchOverlay} />
      </div>
    </div>
  );
}

export default App;
