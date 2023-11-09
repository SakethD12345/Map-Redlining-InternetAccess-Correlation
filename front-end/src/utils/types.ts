export interface ServerErrorResponse {
  result: string;
  message: string;
}

/**
 * Type guard function for checking if given object is a ServerErrorResponse
 */
export function isServerErrorResponse(
  rjson: any
): rjson is ServerErrorResponse {
  if ("result" in rjson)
    console.log("Response contains result " + rjson.result);
  if ("message" in rjson)
    console.log("Response contains message " + rjson.message);

  if (!("result" in rjson)) return false;
  if (!("message" in rjson)) return false;
  return true;
}

export interface MapsServerResponse {
  result: string;
  data: string;
}

export function isMapsServerResponse(rjson: any): rjson is MapsServerResponse {
  if ("result" in rjson)
    console.log("Response contains result " + rjson.result);
  if ("data" in rjson) console.log("Response contains data");

  if (!("result" in rjson)) return false;
  if (!("data" in rjson)) return false;
  // rjson.data = JSON.parse(rjson.data);
  return true;
}