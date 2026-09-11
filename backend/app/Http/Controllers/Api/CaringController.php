<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\CaringStoreRequest;
use App\Http\Resources\CaringResource;
use App\Models\Caring;
use App\Models\Nurse;
use App\Models\Caringtype;
use App\Models\Patient;
use App\Traits\ApiResponseTrait;
use App\Traits\CaringTrait;
use Illuminate\Http\Request;

class CaringController extends Controller
{
    use CaringTrait, ApiResponseTrait;


    /**
     * @OA\Get(
     *     path="/api/carings",
     *     tags={"Caring"},
     *     summary="Get Carings",
     *     description="Get Carings",
     *     operationId="getCarings",
     *     deprecated=false,
     *     @OA\Response(response=200,description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *    ),
     *   @OA\Response(response="404",description="No Carings was found"),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getCarings()
    {
       $carings = CaringResource::collection(Caring::get());
       if(!$carings)
       {
           return $this->apiResponse(null, 404,'No Carings was found');
       }
       return $this->apiResponse($carings, 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/carings/trashed",
     *     tags={"Caring"},
     *     summary="Get Trashed Carings",
     *     description="Get Trashed Carings",
     *     operationId="getTrashed",
     *     deprecated=false,
     *     @OA\Response(response=200,description="successful operation",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *    ),
     *   @OA\Response(response="404",description="No Carings was found"),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getTrashed()
    {
        $carings = CaringResource::collection(Caring::onlyTrashed()->get());
        if(!$carings)
        {
            return $this->apiResponse(null, 404,'No Carings was found');
        }
        return $this->apiResponse($carings, 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/carings/trashed/{id}",
     *     tags={"Caring"},
     *     summary="Get Trashed Caring",
     *     description="Caring id should be provided",
     *     operationId="restoreTrashed",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Get Trashed Caring",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
     *   @OA\Response(response="404",description="A Caring with the specified ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function restoreTrashed(int $id)
    {
        $caring = Caring::withTrashed()->find($id);
		if(!$caring)
		{
			return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
		}
        $caring->is_finished = false;
        $caring->save();
        $caring->restore();
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }

    /**
     * @OA\Patch(
     *   path="/api/carings/trashed",
     *   tags={"Caring"},
     *   summary="Update Trashed Caring",
     *   description="Update Trashed Caring",
     *   operationId="updateTrashed",
     *   @OA\Response(response="201",description="Caring updated",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="data",
     *       property="data",
     *       type="object",
     *       ref="#/components/schemas/Caring"
     *       ),
     *     ),
     *   ),
     *   @OA\Response(response="400",description="Bad request."),
     *   @OA\Response(response="404",description="A Caring with the specified ID was not found."),
     *   @OA\RequestBody(
     *     description="Trashed Caring to update",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Caring")
     *   ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function updateTrashed(Request $request)
    {
        $caring = Caring::withTrashed()->find($request->id);
		if(!$caring)
		{
			return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
		}
        $this->saveRequestData($request, $caring);
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }

    /**
     * @OA\Delete(
     *  path="/api/carings/prune/{id}",
     *  summary="Prune Caring",
     *  description="Prune Caring",
     *  operationId="pruneCaring",
     *  tags={"Caring"},
     *  @OA\Parameter(ref="#/components/parameters/Caring--id"),
     *  @OA\Response(response=200,description="ok"),
     *  @OA\Response(response=401,description="Unauthorized"),
     *  @OA\Response(response=404,description="A Caring with the specified ID was not found."),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function pruneCaring(int $id)
    {
        $caring = Caring::withTrashed()->find($id);
		if(!$caring)
		{
			return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
		}
		$caring->forceDelete();
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/carings/{id}",
     *     tags={"Caring"},
     *     summary="Get Caring",
     *     description="Caring id should be provided",
     *     operationId="getCaring",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Get Caring",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
     *   @OA\Response(response=404,description="A Caring with the specified ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getCaring(int $id)
    {
        $caring = Caring::find($id);
        if(!$caring){
           return $this->apiResponse(null, 404, "A Caring with the specified ID was not found.");
        }
        return $this->apiResponse(new CaringResource($caring), 200,"ok");
    }

    /**
     * @OA\Get(
     *     path="/api/carings/nurse/{nurse_id}",
     *     tags={"Caring"},
     *     summary="Get Caring by nurse id",
     *     description="Nurse id should be provided",
     *     operationId="getByNurseId",
     *     @OA\Parameter(
     *       name="nurse_id",
     *       in="path",
     *       description="Nurse Id",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
	 *   @OA\Response(response=401,description="Unauthorized"),
     *   @OA\Response(response=404,description="Carings with the specified Nurse ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getByNurseId(int $nurseid)
    {
		if(auth('api')->user()->is_admin)
		{
			$carings = CaringResource::collection(Nurse::with('carings')->find($nurseid)->carings);
			if($carings->isEmpty())
			{
				return $this->apiResponse(null, 404,'A Caring with the specified Nurse ID was not found.');
			}
			return $this->apiResponse($carings, 200,'ok');
		}
		else
		{
			if($nurseid == auth('api')->user()->id)
			{
		        $carings = CaringResource::collection(Nurse::with('carings')->find($nurseid)->carings);
				if($carings->isEmpty())
				{
					return $this->apiResponse(null, 404,'No Carings was found');
				}
				return $this->apiResponse($carings, 200,'ok');
			}
			else
			{
				return $this->apiResponse(null, 401,'you are not authorized');
			}
		}
    }

    /**
     * @OA\Get(
     *     path="/api/carings/caringtype/{caringtype_id}",
     *     tags={"Caring"},
     *     summary="Get Caring by caringtype id",
     *     description="caringtype id should be provided",
     *     operationId="getByCaringTypeId",
     *     @OA\Parameter(
     *       name="caringtype_id",
     *       in="path",
     *       description="CaringType Id",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
     *   @OA\Response(response=404,description="Carings with the specified Caringtype ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getByCaringTypeId(int $caringtypeid)
    {
		$carings = CaringResource::collection(Caringtype::with('carings')->find($caringtypeid)->carings);
        if($carings->isEmpty())
        {
            return $this->apiResponse(null, 404,'A Caring with the Caringtype ID was not found.');
        }
        return $this->apiResponse($carings, 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/carings/patient/{patient_id}",
     *     tags={"Caring"},
     *     summary="Get Caring by patient id",
     *     description="Patient id should be provided",
     *     operationId="getByPatientId",
     *     @OA\Parameter(
     *       name="patient_id",
     *       in="path",
     *       description="Patient Id",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
     *   @OA\Response(response=404,description="Carings with the specified Patient ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getByPatientId(int $patientid)
    {
        $carings = CaringResource::collection(Patient::with('carings')->find($patientid)->carings);
			if($carings->isEmpty())
			{
				return $this->apiResponse(null, 404,'Carings with the specified Patient ID was not found.');
			}
			return $this->apiResponse($carings, 200,'ok');
    }

    /**
     * @OA\Post(
     *   path="/api/carings",
     *   tags={"Caring"},
     *   summary="Create Caring",
     *   description="Create Caring",
     *   operationId="createCaring",
     *   @OA\RequestBody(
     *     description="Create Caring",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Caring")
     *   ),
     *  @OA\Response(response="201",description="Caring created",
     *   @OA\JsonContent(
     *     @OA\Property(
     *      title="data",
     *      property="data",
     *      type="object",
     *      ref="#/components/schemas/Caring"
     *     ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function createCaring(CaringStoreRequest $request)
    {
        $caring = new Caring();
        $this->saveRequestData($request,$caring);
        return $this->apiResponse(new CaringResource($caring), 201,'Caring created');
    }

    /**
     * @OA\Patch(
     *   path="/api/carings",
     *   tags={"Caring"},
     *   summary="Update Caring",
     *   description="Update Caring",
     *   operationId="updateCaring",
     *   @OA\Response(response="201",description="Caring updated",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="data",
     *       property="data",
     *       type="object",
     *       ref="#/components/schemas/Caring"
     *       ),
     *     ),
     *   ),
     *   @OA\Response(response="400",description="Bad request."),
     *   @OA\Response(response="404",description="A Caring with the specified ID was not found."),
     *   @OA\RequestBody(
     *     description="Caring to update",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Caring")
     *   ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function updateCaring(CaringStoreRequest $request)
    {
        $caring = Caring::find($request->id);
		if(!$caring)
		{
			return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
		}
        $this->saveRequestData($request,$caring);
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }

    /**
     * @OA\Delete(
     *  path="/api/carings/{id}",
     *  summary="Delete Caring",
     *  description="Delete Caring",
     *  operationId="deleteCaring",
     *  tags={"Caring"},
     *  @OA\Parameter(ref="#/components/parameters/Caring--id"),
     *  @OA\Response(response=200,description="ok"),
     *  @OA\Response(response=401,description="Unauthorized"),
     *  @OA\Response(response="404",description="A Caring with the specified ID was not found."),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function deleteCaring(int $id)
    {
        $caring = Caring::find($id);
        if(!$caring)
        {
            return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
        }
        $caring->delete();
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/carings/check/{id}",
     *     tags={"Caring"},
     *     summary="Check Caring",
     *     description="Patient id should be provided",
     *     operationId="checkCaring",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Caring Id",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caring")
     *       )
     *     ),
     *   @OA\Response(response="404",description="A Patient with the specified ID was not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function checkCaring(int $id){
        $caring = Caring::withTrashed()->find($id);
		if(!$caring)
		{
			return $this->apiResponse(null, 404,'A Caring with the specified ID was not found.');
		}
        $caring->is_finished = true;
        $caring->save();
        $caring->delete();
        return $this->apiResponse(new CaringResource($caring), 200,'ok');
    }
}
